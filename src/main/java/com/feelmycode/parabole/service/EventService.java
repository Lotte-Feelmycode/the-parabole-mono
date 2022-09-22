package com.feelmycode.parabole.service;

import com.feelmycode.parabole.domain.Coupon;
import com.feelmycode.parabole.domain.Event;
import com.feelmycode.parabole.domain.EventPrize;
import com.feelmycode.parabole.domain.Product;
import com.feelmycode.parabole.domain.Seller;
import com.feelmycode.parabole.dto.EventCreateRequestDto;
import com.feelmycode.parabole.dto.EventDto;
import com.feelmycode.parabole.dto.EventPrizeCreateRequestDto;
import com.feelmycode.parabole.global.error.IdNotFoundException;
import com.feelmycode.parabole.repository.CouponRepository;
import com.feelmycode.parabole.repository.EventRepository;
import com.feelmycode.parabole.repository.ProductRepository;
import com.feelmycode.parabole.repository.SellerRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final SellerRepository sellerRepository;

    private final CouponRepository couponRepository;

    private final ProductRepository productRepository;

    /**
     *  이벤트 생성
     */
    @Transactional
    public Long Event(EventCreateRequestDto eventDto) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 엔티티 조회
        Seller seller = sellerRepository.findById(eventDto.getSellerId()).orElseThrow(() -> new IdNotFoundException("해당하는 ID의 판매자가 없습니다."));

        List<Long> productIds = eventDto.getEventPrizeCreateRequestDtos().getProductIds();
        List<Long> couponIds  = eventDto.getEventPrizeCreateRequestDtos().getCouponIds();

        List<Product> products = new ArrayList<>();
        List<Coupon> coupons = new ArrayList<>();

        // 이벤트-경품정보 생성
        List<EventPrize> eventPrizeList = new ArrayList<>();

        if (CollectionUtils.isEmpty(productIds)) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId).orElseThrow(() -> new IdNotFoundException("해당하는 ID의 상품이 없습니다."));
                EventPrize productPrize = new EventPrize("PRODUCT", eventDto.getEventPrizeCreateRequestDtos().getStock(), product);
            }
        }

        if (CollectionUtils.isEmpty(productIds)) {
            for (Long couponId : couponIds) {
                Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new IdNotFoundException("해당하는 ID의 쿠폰이 없습니다."));
                EventPrize couponPrize = new EventPrize("COUPON", eventDto.getEventPrizeCreateRequestDtos().getStock(), coupon);
                eventPrizeList.add(couponPrize);
            }
        }

        // 이벤트 생성
        Event event = Event.builder()
            .seller(seller)
            .createdBy(eventDto.getCreatedBy())
            .type(eventDto.getType())
            .title(eventDto.getTitle())
            .startAt(LocalDateTime.parse(eventDto.getStartAt(), formatter))
            .endAt(LocalDateTime.parse(eventDto.getEndAt(), formatter))
            .descript(eventDto.getDescript())
            .eventImage(eventDto.getEventImage())
            .eventPrizes(eventPrizeList)
            .build();

        // 이벤트 저장
        eventRepository.save(event);
        return event.getId();
    }

    /**
     * 이벤트 단건 조회
     */
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow();
    }

    /**
     * 이벤트 전체 조회
     */
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    // TODO : 이벤트 수정

    /**
     * 이벤트 취소
     */
    @Transactional
    public void cancelEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        event.ifPresent(e -> {
            e.cancel();
            eventRepository.save(e);
        });
    }
}
