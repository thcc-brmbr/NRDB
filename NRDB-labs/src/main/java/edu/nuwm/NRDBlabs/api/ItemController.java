package edu.nuwm.NRDBlabs.api;

import edu.nuwm.NRDBlabs.persistence.entity.Item;
import edu.nuwm.NRDBlabs.persistence.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<String> getItemNames() {
        return itemRepository.findAll().stream().map(Item::getName).collect(Collectors.toList());
    }
}
