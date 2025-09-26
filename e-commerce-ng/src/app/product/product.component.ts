import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Product } from '../models/product.model';
import { ProductService } from '../services/product.service';
import { Message } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { StyleClassModule } from 'primeng/styleclass';
import { FluidModule } from 'primeng/fluid';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss'],
  imports: [CardModule, Message, FormsModule, TableModule, ButtonModule, InputTextModule, StyleClassModule, FluidModule, InputGroupModule, InputGroupAddonModule]
})
export class ProductComponent implements OnInit {
  products: Product[] = [];
  product: Product = { productId: 0, name: '', description: '', price: 0 };
  loading = false;
  msgs: any[] = [];

  constructor(private productService: ProductService) { }

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: data => { this.products = data; this.loading = false; },
      error: err => {
        this.handleHttpError(err, 'loading products');
        this.loading = false;
      }
    });
  }

  saveProduct() {
    if (this.product.productId) {
      this.productService.update(this.product.productId, this.product).subscribe({
        next: () => { this.showSuccess('Product updated'); this.resetForm(); this.loadProducts(); },
        error: err => this.handleHttpError(err, 'updating product')
      });
    } else {
      this.productService.create(this.product).subscribe({
        next: () => { this.showSuccess('Product created'); this.resetForm(); this.loadProducts(); },
        error: err => this.handleHttpError(err, 'creating product')
      });
    }
  }

  editProduct(p: Product) {
    this.product = { ...p };
  }

  deleteProduct(id: number) {
    this.productService.delete(id).subscribe({
      next: () => { this.showSuccess('Product deleted'); this.loadProducts(); },
      error: err => this.handleHttpError(err, 'deleting product')
    });
  }

  resetForm() {
    this.product = { productId: 0, name: '', description: '', price: 0 };
  }

  showSuccess(msg: string) {
    this.msgs = [{ severity: 'success', text: msg }];
  }

  showError(msg: string) {
    this.msgs = [{ severity: 'error', text: msg }];
  }

  handleHttpError(err: any, action: string) {
    if (err.status === 401 || err.status === 403) {
      this.showError('Authentication error: You are not authorized.');
    } else if (err.status === 400 && err.error && err.error.error) {
      this.showError('Validation error: ' + err.error.error);
    } else if (err.status === 400 && err.error && typeof err.error === 'string') {
      this.showError('Validation error: ' + err.error);
    } else {
      this.showError('Error ' + action);
    }
  }
}
