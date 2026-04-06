import { Injectable } from '@angular/core';
import Keycloak, { KeycloakConfig, KeycloakInitOptions } from 'keycloak-js';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak?: Keycloak;
  // Backend exposes environment-specific OIDC settings so frontend builds stay static.
  private configUrl = '/api/auth/config';

  constructor(private http: HttpClient) {}

  async init(): Promise<void> {
    const config = await firstValueFrom(this.http.get<any>(this.configUrl));
    const keycloakConfig: KeycloakConfig = {
      url: config.authHost,
      realm: config.realm,
      clientId: config.clientId,
    };
    this.keycloak = new Keycloak(keycloakConfig);
    const options: KeycloakInitOptions = {
      // check-sso keeps existing sessions without forcing an interactive redirect on app load.
      onLoad: 'check-sso',
      checkLoginIframe: false,
    };
    await this.keycloak.init(options);
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  isLoggedIn(): boolean {
    return !!this.keycloak?.authenticated;
  }

  login(): void {
    this.keycloak?.login();
  }

  logout(): void {
    this.keycloak?.logout();
  }
}
