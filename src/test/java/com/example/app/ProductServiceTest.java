package com.example.app;

import com.example.app.entity.Product;
import com.example.app.repository.ProductRepository;
import com.example.app.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setName("Laptop");
        sampleProduct.setDescription("A great laptop");
        sampleProduct.setPrice(999.99);
    }

    @Test
    void findAll_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));
        List<Product> result = productService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void findById_existingId_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        Product result = productService.findById(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsProduct() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);
        Product result = productService.create(sampleProduct);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void delete_existingId_deletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        productService.delete(1L);
        verify(productRepository).delete(sampleProduct);
    }
}
