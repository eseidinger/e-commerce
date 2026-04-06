import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { OrderComponent } from './order.component';
import { OrderService } from '../services/order.service';
import { Order } from '../models/order.model';
import { vi } from 'vitest';

describe('OrderComponent', () => {
  let component: OrderComponent;
  let fixture: ComponentFixture<OrderComponent>;
  let orderServiceSpy: {
    getAll: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    orderServiceSpy = {
      getAll: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    };

    orderServiceSpy.getAll.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [OrderComponent],
      providers: [{ provide: OrderService, useValue: orderServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load orders and convert orderDate values to Date', () => {
    const apiOrder = {
      orderId: 1,
      customerId: 2,
      orderDate: '2026-04-01T00:00:00.000Z',
      totalAmount: 42,
    } as unknown as Order;
    orderServiceSpy.getAll.mockReturnValue(of([apiOrder]));

    component.loadOrders();

    expect(component.orders.length).toBe(1);
    expect(component.orders[0].orderDate instanceof Date).toBe(true);
  });

  it('should create order when orderId is not set', () => {
    component.order = {
      orderId: 0,
      customerId: 2,
      orderDate: new Date('2026-04-02T10:30:00.000Z'),
      totalAmount: 50,
    };
    orderServiceSpy.create.mockReturnValue(of(component.order));

    component.saveOrder();

    expect(orderServiceSpy.create).toHaveBeenCalled();
    expect(component.msgs[0].severity).toBe('success');
  });

  it('should show auth error for 403 responses', () => {
    orderServiceSpy.getAll.mockReturnValue(throwError(() => ({ status: 403 })));

    component.loadOrders();

    expect(component.msgs[0].severity).toBe('error');
    expect(component.msgs[0].text).toContain('not authorized');
  });
});
