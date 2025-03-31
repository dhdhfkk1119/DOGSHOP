package com.mysite.jjw.service;

import com.mysite.jjw.Handler.DataNotFoundException;
import com.mysite.jjw.entity.Product;
import com.mysite.jjw.entity.Product_buy;
import com.mysite.jjw.entity.Review;
import com.mysite.jjw.repository.ProductBuyRepository;
import com.mysite.jjw.repository.ProductRepository;
import com.mysite.jjw.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductBuyRepository productBuyRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    private final String uploadURL = "C:\\Project\\jjw\\src\\main\\resources\\static\\review-images"; // 이미지 업로드 경로

    // 상품 상세 페이지 정보 가져오기
    public Product getReviewinfo(Long product_idx) {
        // product_idx를 사용하여 Product_buy 정보를 조회
        Optional<Product_buy> productBuyOpt = productBuyRepository.findById(product_idx);

        // Product_buy가 존재하지 않으면 예외 처리
        if (!productBuyOpt.isPresent()) {
            throw new DataNotFoundException("Product buy data not found");
        }

        Product_buy productBuy = productBuyOpt.get(); // Product_buy 객체를 꺼냄

        // Product_buy에서 productBuyProductidx 값을 가져와서, 해당 상품 정보 조회
        Optional<Product> productOpt = this.productRepository.findById(productBuy.getProductBuyProductidx());

        // 상품이 존재하지 않으면 예외 처리
        if (!productOpt.isPresent()) {
            throw new DataNotFoundException("Product not found");
        }

        return productOpt.get(); // Product 객체 반환
    }


    // 상품 수량 정보 가져오기
    public Product_buy getQuantity(Long product_idx, Long useridx) {
        // 상품 조회
        Optional<Product_buy> productOpt = this.productBuyRepository.findByProductBuyIdxAndProductBuyUseridx(product_idx,useridx);  // ✅ 수정됨

        if (productOpt.isEmpty()) {
            throw new DataNotFoundException("Product not found"); // 상품이 없으면 예외 처리
        }

        Product_buy productbuy = productOpt.get();

        return productbuy;
    }

    //리뷰 등록하기
    @Transactional
    public Review registerReview(Review review, MultipartFile[] files) throws Exception {

        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty() || file.getSize() <= 0) {
                continue;
            }
            String originalName = file.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
            String uuid = UUID.randomUUID().toString();
            String newFileName = uuid + "_" + fileName;
            Path savePath = Paths.get(uploadURL, newFileName);
            file.transferTo(savePath.toFile());
            fileNames.add(newFileName);
        }
        // 1. 해당하는 상품 찾기
        Product product = productRepository.findById(review.getReviewProductidx())
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다"));

        // 2. 상품의 댓글 개수 증가
        product.setProduct_coment(product.getProduct_coment() + 1);
        productRepository.save(product);  // 변경 사항 저장

        // 리뷰 등록하기
        review.setReviewContent(review.getReviewContent());
        review.setReviewAt(LocalDateTime.now());
        review.setReviewUser(review.getReviewUser());
        review.setReviewProductidx(review.getReviewProductidx());
        review.setReviewProductbuyidx(review.getReviewProductbuyidx());

        //  이미지가 없으면 null 저장
        if (fileNames.isEmpty()) {
            review.setReviewImage(null);
        } else {
            review.setReviewImage(String.join(",", fileNames));
        }
        review.setReviewScope(review.getReviewScope());

        return reviewRepository.save(review);
    }
}
