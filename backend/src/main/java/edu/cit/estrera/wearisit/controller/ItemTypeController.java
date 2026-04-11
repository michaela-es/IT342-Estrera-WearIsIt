package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.api.ApiResponse;
import edu.cit.estrera.wearisit.dto.ItemTypeDto;
import edu.cit.estrera.wearisit.service.ItemTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-types")
@RequiredArgsConstructor
public class ItemTypeController {

    private final ItemTypeService itemTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemTypeDto>>> getAllTypes() {
        List<ItemTypeDto> types = itemTypeService.getAllTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }
}