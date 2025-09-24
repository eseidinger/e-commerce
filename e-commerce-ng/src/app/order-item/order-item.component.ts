import { Component, OnInit } from '@angular/core';
import { OrderItem } from '../models/order-item.model';
import { OrderItemService } from '../services/order-item.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-order-item',
  templateUrl: './order-item.component.html',
  styleUrls: ['./order-item.component.scss'],
  providers: [MessageService]
})
export class OrderItemComponent implements OnInit {
  orderItems: OrderItem[] = [];
  orderItem: OrderItem = { orderItemId: 0, orderId: 0, productId: 0, quantity: 0, price: 0 };
  loading = false;

  constructor(private orderItemService: OrderItemService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadOrderItems();
  }

  loadOrderItems() {
    this.loading = true;
    this.orderItemService.getAll().subscribe({
      next: data => { this.orderItems = data; this.loading = false; },
      error: err => { this.showError('Error loading order items'); this.loading = false; }
    });
  }

  saveOrderItem() {
    if (this.orderItem.orderItemId) {
      this.orderItemService.update(this.orderItem.orderItemId, this.orderItem).subscribe({
        next: () => { this.showSuccess('Order item updated'); this.resetForm(); this.loadOrderItems(); },
        error: () => this.showError('Error updating order item')
      });
    } else {
      this.orderItemService.create(this.orderItem).subscribe({
        next: () => { this.showSuccess('Order item created'); this.resetForm(); this.loadOrderItems(); },
        error: () => this.showError('Error creating order item')
      });
    }
  }

  editOrderItem(oi: OrderItem) {
    this.orderItem = { ...oi };
  }

  deleteOrderItem(id: number) {
    this.orderItemService.delete(id).subscribe({
      next: () => { this.showSuccess('Order item deleted'); this.loadOrderItems(); },
      error: () => this.showError('Error deleting order item')
    });
  }

  resetForm() {
    this.orderItem = { orderItemId: 0, orderId: 0, productId: 0, quantity: 0, price: 0 };
  }

  showSuccess(msg: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
  }

  showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
