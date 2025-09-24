import { Component, OnInit } from '@angular/core';
import { Customer } from '../models/customer.model';
import { CustomerService } from '../services/customer.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss'],
  providers: [MessageService]
})
export class CustomerComponent implements OnInit {
  customers: Customer[] = [];
  customer: Customer = { customerId: 0, name: '', email: '', address: '' };
  loading = false;

  constructor(private customerService: CustomerService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getAll().subscribe({
      next: data => { this.customers = data; this.loading = false; },
      error: err => { this.showError('Error loading customers'); this.loading = false; }
    });
  }

  saveCustomer() {
    if (this.customer.customerId) {
      this.customerService.update(this.customer.customerId, this.customer).subscribe({
        next: () => { this.showSuccess('Customer updated'); this.resetForm(); this.loadCustomers(); },
        error: () => this.showError('Error updating customer')
      });
    } else {
      this.customerService.create(this.customer).subscribe({
        next: () => { this.showSuccess('Customer created'); this.resetForm(); this.loadCustomers(); },
        error: () => this.showError('Error creating customer')
      });
    }
  }

  editCustomer(c: Customer) {
    this.customer = { ...c };
  }

  deleteCustomer(id: number) {
    this.customerService.delete(id).subscribe({
      next: () => { this.showSuccess('Customer deleted'); this.loadCustomers(); },
      error: () => this.showError('Error deleting customer')
    });
  }

  resetForm() {
    this.customer = { customerId: 0, name: '', email: '', address: '' };
  }

  showSuccess(msg: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
  }

  showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
