import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { OrderItemComponent } from './order-item.component';
import { OrderItemService } from '../services/order-item.service';
import { OrderItem } from '../models/order-item.model';
import { vi } from 'vitest';

describe('OrderItemComponent', () => {
  let component: OrderItemComponent;
  let fixture: ComponentFixture<OrderItemComponent>;
  let orderItemServiceSpy: {
    getAll: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    orderItemServiceSpy = {
      getAll: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    };

    orderItemServiceSpy.getAll.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [OrderItemComponent],
      providers: [{ provide: OrderItemService, useValue: orderItemServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load order items on init', () => {
    const orderItems: OrderItem[] = [
      { orderItemId: 1, orderId: 2, productId: 3, quantity: 1, price: 10 },
    ];
    orderItemServiceSpy.getAll.mockReturnValue(of(orderItems));

    component.loadOrderItems();

    expect(component.orderItems.length).toBe(1);
    expect(component.orderItems[0].orderId).toBe(2);
  });

  it('should create order item when id is not set', () => {
    component.orderItem = { orderItemId: 0, orderId: 2, productId: 3, quantity: 2, price: 19.99 };
    orderItemServiceSpy.create.mockReturnValue(of(component.orderItem));

    component.saveOrderItem();

    expect(orderItemServiceSpy.create).toHaveBeenCalled();
    expect(component.msgs[0].severity).toBe('success');
  });

  it('should show generic error on unexpected response', () => {
    orderItemServiceSpy.delete.mockReturnValue(throwError(() => ({ status: 500 })));

    component.deleteOrderItem(1);

    expect(component.msgs[0].severity).toBe('error');
    expect(component.msgs[0].text).toContain('deleting order item');
  });
});
