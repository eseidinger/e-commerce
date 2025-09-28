import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../models/review.model';

import { AuthService } from '../auth.service';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = '/api/reviews';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private getAuthHeaders(): { headers: HttpHeaders } | {} {
    const token = this.authService.getToken();
    return token ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) } : {};
  }

  getAll(): Observable<Review[]> {
    return this.http.get<Review[]>(this.apiUrl, this.getAuthHeaders());
  }

  getById(id: number): Observable<Review> {
    return this.http.get<Review>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }

  create(review: Review): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, review, this.getAuthHeaders());
  }

  update(id: number, review: Review): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}`, review, this.getAuthHeaders());
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }
}
