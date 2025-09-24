import { Component, OnInit } from '@angular/core';
import { Product } from '../models/product.model';
import { ProductService } from '../services/product.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss'],
  providers: [MessageService]
})
export class ProductComponent implements OnInit {
  products: Product[] = [];
  product: Product = { productId: 0, name: '', description: '', price: 0 };
  loading = false;

  constructor(private productService: ProductService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: err => { this.showError('Error loading products'); this.loading = false; }
    });
  }

  saveProduct() {
    if (this.product.productId) {
      this.productService.update(this.product.productId, this.product).subscribe({
        next: () => { this.showSuccess('Product updated'); this.resetForm(); this.loadProducts(); },
        error: () => this.showError('Error updating product')
      });
    } else {
      this.productService.create(this.product).subscribe({
        next: () => { this.showSuccess('Product created'); this.resetForm(); this.loadProducts(); },
        error: () => this.showError('Error creating product')
      });
    }
  }

  editProduct(p: Product) {
    this.product = { ...p };
  }

  deleteProduct(id: number) {
    this.productService.delete(id).subscribe({
      next: () => { this.showSuccess('Product deleted'); this.loadProducts(); },
      error: () => this.showError('Error deleting product')
    });
  }

  resetForm() {
    this.product = { productId: 0, name: '', description: '', price: 0 };
  }

  showSuccess(msg: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
  }

  showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
