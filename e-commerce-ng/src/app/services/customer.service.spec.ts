import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';
import { AuthService } from '../auth.service';
import { Customer } from '../models/customer.model';
import { vi } from 'vitest';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;
  let authServiceSpy: { getToken: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceSpy = {
      getToken: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        CustomerService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceSpy },
      ],
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should send Authorization header when token is available', () => {
    authServiceSpy.getToken.mockReturnValue('jwt-token');

    service.getAll().subscribe();

    const req = httpMock.expectOne('/api/customers');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-token');
    req.flush([]);
  });

  it('should create customer via POST', () => {
    authServiceSpy.getToken.mockReturnValue(undefined);
    const customer: Customer = {
      customerId: 0,
      name: 'Alice',
      email: 'alice@example.com',
      address: 'Main Street 1',
    };

    service.create(customer).subscribe((result) => {
      expect(result.name).toBe('Alice');
    });

    const req = httpMock.expectOne('/api/customers');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(customer);
    req.flush(customer);
  });
});
