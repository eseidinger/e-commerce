import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ReviewComponent } from './review.component';
import { ReviewService } from '../services/review.service';
import { Review } from '../models/review.model';
import { vi } from 'vitest';

describe('ReviewComponent', () => {
  let component: ReviewComponent;
  let fixture: ComponentFixture<ReviewComponent>;
  let reviewServiceSpy: {
    getAll: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    reviewServiceSpy = {
      getAll: vi.fn(),
      create: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    };

    reviewServiceSpy.getAll.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ReviewComponent],
      providers: [{ provide: ReviewService, useValue: reviewServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load reviews and convert reviewDate to Date', () => {
    const reviews = [
      {
        reviewId: 1,
        productId: 2,
        customerId: 3,
        rating: 5,
        comment: 'Great',
        reviewDate: '2026-04-01T00:00:00.000Z',
      } as unknown as Review,
    ];
    reviewServiceSpy.getAll.mockReturnValue(of(reviews));

    component.loadReviews();

    expect(component.reviews.length).toBe(1);
    expect(component.reviews[0].reviewDate instanceof Date).toBe(true);
  });

  it('should create review when id is not set', () => {
    component.review = {
      reviewId: 0,
      productId: 2,
      customerId: 3,
      rating: 4,
      comment: 'Good',
      reviewDate: new Date('2026-04-03T10:00:00.000Z'),
    };
    reviewServiceSpy.create.mockReturnValue(of(component.review));

    component.saveReview();

    expect(reviewServiceSpy.create).toHaveBeenCalled();
    expect(component.msgs[0].severity).toBe('success');
  });

  it('should show unauthorized error for 403 responses', () => {
    reviewServiceSpy.getAll.mockReturnValue(throwError(() => ({ status: 403 })));

    component.loadReviews();

    expect(component.msgs[0].severity).toBe('error');
    expect(component.msgs[0].text).toContain('not authorized');
  });
});
