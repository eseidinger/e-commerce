import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderItem } from '../models/order-item.model';
import { OrderItemService } from '../services/order-item.service';
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
  selector: 'app-order-item',
  templateUrl: './order-item.component.html',
  styleUrls: ['./order-item.component.scss'],
  imports: [
    CardModule,
    Message,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    StyleClassModule,
    FluidModule,
    InputGroupModule,
    InputGroupAddonModule,
  ],
})
export class OrderItemComponent implements OnInit {
  orderItems: OrderItem[] = [];
  orderItem: OrderItem = { orderItemId: 0, orderId: 0, productId: 0, quantity: 0, price: 0 };
  loading = false;
  msgs: any[] = [];

  constructor(private orderItemService: OrderItemService) {}

  ngOnInit() {
    this.loadOrderItems();
  }

  loadOrderItems() {
    this.loading = true;
    this.orderItemService.getAll().subscribe({
      next: (data) => {
        this.orderItems = data;
        this.loading = false;
      },
      error: (err) => {
        this.handleHttpError(err, 'loading order items');
        this.loading = false;
      },
    });
  }

  saveOrderItem() {
    if (this.orderItem.orderItemId) {
      this.orderItemService.update(this.orderItem.orderItemId, this.orderItem).subscribe({
        next: () => {
          this.showSuccess('Order item updated');
          this.resetForm();
          this.loadOrderItems();
        },
        error: (err) => this.handleHttpError(err, 'updating order item'),
      });
    } else {
      this.orderItemService.create(this.orderItem).subscribe({
        next: () => {
          this.showSuccess('Order item created');
          this.resetForm();
          this.loadOrderItems();
        },
        error: (err) => this.handleHttpError(err, 'creating order item'),
      });
    }
  }

  editOrderItem(oi: OrderItem) {
    this.orderItem = { ...oi };
  }

  deleteOrderItem(id: number) {
    this.orderItemService.delete(id).subscribe({
      next: () => {
        this.showSuccess('Order item deleted');
        this.loadOrderItems();
      },
      error: (err) => this.handleHttpError(err, 'deleting order item'),
    });
  }

  resetForm() {
    this.orderItem = { orderItemId: 0, orderId: 0, productId: 0, quantity: 0, price: 0 };
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
