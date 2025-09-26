import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Customer } from '../models/customer.model';
import { CustomerService } from '../services/customer.service';
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
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss'],
  imports: [CardModule, Message, FormsModule, TableModule, ButtonModule, InputTextModule, StyleClassModule, FluidModule, InputGroupModule, InputGroupAddonModule]
})
export class CustomerComponent implements OnInit {
  customers: Customer[] = [];
  customer: Customer = { customerId: 0, name: '', email: '', address: '' };
  loading = false;
  msgs: any[] = [];

  constructor(private customerService: CustomerService) { }

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getAll().subscribe({
      next: data => { this.customers = data; this.loading = false; },
      error: err => {
        this.handleHttpError(err, 'loading customers');
        this.loading = false;
      }
    });
  }

  saveCustomer() {
    if (this.customer.customerId) {
      this.customerService.update(this.customer.customerId, this.customer).subscribe({
        next: () => { this.showSuccess('Customer updated'); this.resetForm(); this.loadCustomers(); },
        error: err => this.handleHttpError(err, 'updating customer')
      });
    } else {
      this.customerService.create(this.customer).subscribe({
        next: () => { this.showSuccess('Customer created'); this.resetForm(); this.loadCustomers(); },
        error: err => this.handleHttpError(err, 'creating customer')
      });
    }
  }

  editCustomer(c: Customer) {
    this.customer = { ...c };
  }

  deleteCustomer(id: number) {
    this.customerService.delete(id).subscribe({
      next: () => { this.showSuccess('Customer deleted'); this.loadCustomers(); },
      error: err => this.handleHttpError(err, 'deleting customer')
    });
  }

  resetForm() {
    this.customer = { customerId: 0, name: '', email: '', address: '' };
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
