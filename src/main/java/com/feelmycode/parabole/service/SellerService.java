//package com.feelmycode.parabole.service;
////import com.feelmycode.parabole.domain.Seller;
////import com.feelmycode.parabole.repository.SellerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class SellerService {
//
//    private final SellerRepository sellerRepository;
//
//    @Transactional
//    public Seller findById(Long sellerId) {
//        return sellerRepository.findById(sellerId).orElseThrow(()-> new IllegalArgumentException());
//    }
//}