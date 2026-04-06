import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { WhoAmIService } from './whoami.service';
import { AuthService } from './auth.service';
import { vi } from 'vitest';

describe('WhoAmIService', () => {
  let service: WhoAmIService;
  let httpMock: HttpTestingController;
  let authServiceSpy: { getToken: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceSpy = {
      getToken: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        WhoAmIService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceSpy },
      ],
    });

    service = TestBed.inject(WhoAmIService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call whoami endpoint with bearer header when token exists', () => {
    authServiceSpy.getToken.mockReturnValue('jwt-token');

    service.getUsername().subscribe((result) => {
      expect(result).toBe('john');
    });

    const req = httpMock.expectOne('/api/auth/whoami');
    expect(req.request.method).toBe('GET');
    expect(req.request.responseType).toBe('text');
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-token');
    req.flush('john');
  });

  it('should call whoami endpoint without Authorization header when token is missing', () => {
    authServiceSpy.getToken.mockReturnValue(undefined);

    service.getUsername().subscribe();

    const req = httpMock.expectOne('/api/auth/whoami');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush('No token!');
  });
});
