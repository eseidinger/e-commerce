import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class WhoAmIService {
  private whoamiUrl = '/api/auth/whoami';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
  ) {}

  getUsername(): Observable<string> {
    const token = this.auth.getToken();
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    return this.http.get(this.whoamiUrl, { responseType: 'text', headers });
  }
}
