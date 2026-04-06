import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ProductComponent } from './product.component';
import { ProductService } from '../services/product.service';
import { Product } from '../models/product.model';
import { vi } from 'vitest';

describe('ProductComponent', () => {
  let component: ProductComponent;
  let fixture: ComponentFixture<ProductComponent>;
  let productServiceSpy: {
    getAll: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    productServiceSpy = {
      getAll: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    };

    productServiceSpy.getAll.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ProductComponent],
      providers: [{ provide: ProductService, useValue: productServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load products on init', () => {
    const products: Product[] = [
      { productId: 1, name: 'Laptop', description: 'Fast', price: 999 },
    ];
    productServiceSpy.getAll.mockReturnValue(of(products));

    component.loadProducts();

    expect(component.products.length).toBe(1);
    expect(component.products[0].name).toBe('Laptop');
  });

  it('should update product when productId is set', () => {
    component.product = { productId: 5, name: 'Phone', description: 'New', price: 700 };
    productServiceSpy.update.mockReturnValue(of(component.product));

    component.saveProduct();

    expect(productServiceSpy.update).toHaveBeenCalled();
    expect(component.msgs[0].severity).toBe('success');
  });

  it('should show validation message from backend', () => {
    productServiceSpy.getAll.mockReturnValue(
      throwError(() => ({ status: 400, error: { error: 'Invalid product price' } })),
    );

    component.loadProducts();

    expect(component.msgs[0].severity).toBe('error');
    expect(component.msgs[0].text).toContain('Validation error');
  });
});
