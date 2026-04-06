import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CustomerComponent } from './customer.component';
import { CustomerService } from '../services/customer.service';
import { Customer } from '../models/customer.model';
import { vi } from 'vitest';

describe('CustomerComponent', () => {
  let component: CustomerComponent;
  let fixture: ComponentFixture<CustomerComponent>;
  let customerServiceSpy: {
    getAll: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    customerServiceSpy = {
      getAll: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    };

    customerServiceSpy.getAll.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [CustomerComponent],
      providers: [{ provide: CustomerService, useValue: customerServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load customers on init', () => {
    const customers: Customer[] = [
      { customerId: 1, name: 'Alice', email: 'alice@example.com', address: 'Main Street 1' },
    ];
    customerServiceSpy.getAll.mockReturnValue(of(customers));

    component.loadCustomers();

    expect(component.customers.length).toBe(1);
    expect(component.customers[0].name).toBe('Alice');
  });

  it('should create customer when customerId is not set', () => {
    component.customer = {
      customerId: 0,
      name: 'Bob',
      email: 'bob@example.com',
      address: 'Second Street 2',
    };
    customerServiceSpy.create.mockReturnValue(of(component.customer));

    component.saveCustomer();

    expect(customerServiceSpy.create).toHaveBeenCalled();
    expect(component.msgs[0].severity).toBe('success');
  });

  it('should show auth error for unauthorized response', () => {
    customerServiceSpy.getAll.mockReturnValue(throwError(() => ({ status: 401 })));

    component.loadCustomers();

    expect(component.msgs[0].severity).toBe('error');
    expect(component.msgs[0].text).toContain('not authorized');
  });
});
