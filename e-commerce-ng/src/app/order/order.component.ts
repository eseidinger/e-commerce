import { Component, OnInit } from '@angular/core';
import { Order } from '../models/order.model';
import { OrderService } from '../services/order.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss'],
  providers: [MessageService]
})
export class OrderComponent implements OnInit {
  orders: Order[] = [];
  order: Order = { orderId: 0, customerId: 0, orderDate: '', totalAmount: 0 };
  loading = false;

  constructor(private orderService: OrderService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService.getAll().subscribe({
      next: data => { this.orders = data; this.loading = false; },
      error: err => { this.showError('Error loading orders'); this.loading = false; }
    });
  }

  saveOrder() {
    if (this.order.orderId) {
      this.orderService.update(this.order.orderId, this.order).subscribe({
        next: () => { this.showSuccess('Order updated'); this.resetForm(); this.loadOrders(); },
        error: () => this.showError('Error updating order')
      });
    } else {
      this.orderService.create(this.order).subscribe({
        next: () => { this.showSuccess('Order created'); this.resetForm(); this.loadOrders(); },
        error: () => this.showError('Error creating order')
      });
    }
  }

  editOrder(o: Order) {
    this.order = { ...o };
  }

  deleteOrder(id: number) {
    this.orderService.delete(id).subscribe({
      next: () => { this.showSuccess('Order deleted'); this.loadOrders(); },
      error: () => this.showError('Error deleting order')
    });
  }

  resetForm() {
    this.order = { orderId: 0, customerId: 0, orderDate: '', totalAmount: 0 };
  }

  showSuccess(msg: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
  }

  showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
