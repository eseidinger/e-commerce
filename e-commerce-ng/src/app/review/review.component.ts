import { Component, OnInit } from '@angular/core';
import { Review } from '../models/review.model';
import { ReviewService } from '../services/review.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.scss'],
  providers: [MessageService]
})
export class ReviewComponent implements OnInit {
  reviews: Review[] = [];
  review: Review = { reviewId: 0, productId: 0, customerId: 0, rating: 0, comment: '', reviewDate: '' };
  loading = false;

  constructor(private reviewService: ReviewService, private messageService: MessageService) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.reviewService.getAll().subscribe({
      next: data => { this.reviews = data; this.loading = false; },
      error: err => { this.showError('Error loading reviews'); this.loading = false; }
    });
  }

  saveReview() {
    if (this.review.reviewId) {
      this.reviewService.update(this.review.reviewId, this.review).subscribe({
        next: () => { this.showSuccess('Review updated'); this.resetForm(); this.loadReviews(); },
        error: () => this.showError('Error updating review')
      });
    } else {
      this.reviewService.create(this.review).subscribe({
        next: () => { this.showSuccess('Review created'); this.resetForm(); this.loadReviews(); },
        error: () => this.showError('Error creating review')
      });
    }
  }

  editReview(r: Review) {
    this.review = { ...r };
  }

  deleteReview(id: number) {
    this.reviewService.delete(id).subscribe({
      next: () => { this.showSuccess('Review deleted'); this.loadReviews(); },
      error: () => this.showError('Error deleting review')
    });
  }

  resetForm() {
    this.review = { reviewId: 0, productId: 0, customerId: 0, rating: 0, comment: '', reviewDate: '' };
  }

  showSuccess(msg: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
  }

  showError(msg: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
  }
}
