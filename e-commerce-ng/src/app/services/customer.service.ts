import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/customer.model';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private apiUrl = '/api/customers';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private getAuthHeaders(): { headers: HttpHeaders } | {} {
    const token = this.authService.getToken();
    return token ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) } : {};
  }

  getAll(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl, this.getAuthHeaders());
  }

  getById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }

  create(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.apiUrl, customer, this.getAuthHeaders());
  }

  update(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.apiUrl}/${id}`, customer, this.getAuthHeaders());
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }
}
