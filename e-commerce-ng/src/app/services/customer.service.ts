import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/customer.model';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private apiUrl = '/api/customers';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getAll(): Observable<Customer[]> {
    const token = this.authService.getToken();
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    return this.http.get<Customer[]>(this.apiUrl, { headers });
  }

  getById(id: number): Observable<Customer> {
    const token = this.authService.getToken();
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    return this.http.get<Customer>(`${this.apiUrl}/${id}`, { headers });
  }

  create(customer: Customer): Observable<Customer> {
    const token = this.authService.getToken();
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    return this.http.post<Customer>(this.apiUrl, customer, { headers });
  }

  update(id: number, customer: Customer): Observable<Customer> {
    const token = this.authService.getToken();
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    return this.http.put<Customer>(`${this.apiUrl}/${id}`, customer, { headers });
  }

  delete(id: number): Observable<void> {
    const token = this.authService.getToken();
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }
}
