import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderItem } from '../models/order-item.model';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class OrderItemService {
  private apiUrl = '/api/order-items';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getAuthHeaders(): Record<string, string> {
    const token = this.authService.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  getAll(): Observable<OrderItem[]> {
    return this.http.get<OrderItem[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getById(id: number): Observable<OrderItem> {
    return this.http.get<OrderItem>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  create(orderItem: OrderItem): Observable<OrderItem> {
    return this.http.post<OrderItem>(this.apiUrl, orderItem, { headers: this.getAuthHeaders() });
  }

  update(id: number, orderItem: OrderItem): Observable<OrderItem> {
    return this.http.put<OrderItem>(`${this.apiUrl}/${id}`, orderItem, { headers: this.getAuthHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
}
