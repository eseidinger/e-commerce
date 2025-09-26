import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = '/api/products';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getAuthHeaders(): Record<string, string> {
    const token = this.authService.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product, { headers: this.getAuthHeaders() });
  }

  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product, { headers: this.getAuthHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }
}
