package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.zerock.shop.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    //                                  <Entity Type Class, primary Key Type>

    List<Item> findByItemNm(String itemNm); // ItemNm 으로 조회

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail); // OR조건으로 조회

    List<Item> findByPriceLessThan(Integer price); // 파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터 조회

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);  // Order by 정렬 처리

    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);


}
