package org.zerock.shop.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.service.ItemService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";

            // itemForm.html 에 에러 보냄
            // $(document).ready(function(){
            //            var errorMessage = [[${errorMessage}]];
            //            if(errorMessage != null){
            //                alert(errorMessage);
            //            }
            //
            //            bindDomEvent();
            //        //  정상일 때 22행 함수 실행
            //
            //        });
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})  //페이징이 없는경우, 있는 경우
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        // 페이지 파라미터가 없으면 0번 페이지를 보임. 한 페이지당 3개의 상품만 보여줌.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);  // 조회 조건, 페이징 정보를 파라미터로 넘겨서 Page 타입으로 받음
        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 item 객체 받음
        model.addAttribute("items", items); // 조회한 상품 데이터 및 페이징정보를 뷰로 전달
        model.addAttribute("itemSearchDto", itemSearchDto); // 페이지 전환시 기존 검색 조건을 유지
        model.addAttribute("maxPage", 5);   // 상품관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수 5

        return "item/itemMng";
        // itemMng.html로 리턴함.
    }

    @GetMapping(value = "/item/{itemId}") // 아이템 상세 페이지
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }





}
