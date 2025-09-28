import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = '/api/orders';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private getAuthHeaders(): Record<string, string> {
    const token = this.authService.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  getAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  create(order: Order): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order, { headers: this.getAuthHeaders() });
  }

  update(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${id}`, order, { headers: this.getAuthHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
}
