import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Order } from '../models/order.model';
import { OrderService } from '../services/order.service';
import { Message } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { StyleClassModule } from 'primeng/styleclass';
import { FluidModule } from 'primeng/fluid';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { DatePickerModule } from 'primeng/datepicker';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss'],
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
    DatePickerModule,
  ],
})
export class OrderComponent implements OnInit {
  orders: Order[] = [];
  order: Order = { orderId: 0, customerId: 0, orderDate: new Date(), totalAmount: 0 };
  loading = false;
  msgs: any[] = [];

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService.getAll().subscribe({
      next: (data) => {
        this.orders = data.map((item) => ({ ...item, orderDate: new Date(item.orderDate) }));
        this.loading = false;
      },
      error: (err) => {
        this.handleHttpError(err, 'loading orders');
        this.loading = false;
      },
    });
  }

  saveOrder() {
    console.log('Original order date:', this.order.orderDate);
    //Convert order date to UTC
    if (this.order.orderDate) {
      const utcDate = new Date(
        Date.UTC(
          this.order.orderDate.getFullYear(),
          this.order.orderDate.getMonth(),
          this.order.orderDate.getDate(),
        ),
      );
      this.order.orderDate = utcDate;
      console.log('Converted order date to UTC:', this.order.orderDate);
    }
    if (this.order.orderId) {
      this.orderService.update(this.order.orderId, this.order).subscribe({
        next: () => {
          this.showSuccess('Order updated');
          this.resetForm();
          this.loadOrders();
        },
        error: (err) => this.handleHttpError(err, 'updating order'),
      });
    } else {
      this.orderService.create(this.order).subscribe({
        next: () => {
          this.showSuccess('Order created');
          this.resetForm();
          this.loadOrders();
        },
        error: (err) => this.handleHttpError(err, 'creating order'),
      });
    }
  }

  editOrder(o: Order) {
    this.order = { ...o, orderDate: new Date(o.orderDate) }; // Create a copy to avoid direct binding issues
  }

  deleteOrder(id: number) {
    this.orderService.delete(id).subscribe({
      next: () => {
        this.showSuccess('Order deleted');
        this.loadOrders();
      },
      error: (err) => this.handleHttpError(err, 'deleting order'),
    });
  }

  resetForm() {
    this.order = { orderId: 0, customerId: 0, orderDate: new Date(), totalAmount: 0 };
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
