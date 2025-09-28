import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Review } from '../models/review.model';
import { ReviewService } from '../services/review.service';
import { Message } from 'primeng/message';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { StyleClassModule } from 'primeng/styleclass';
import { FluidModule } from 'primeng/fluid';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.scss'],
  imports: [
    CardModule,
    Message,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    StyleClassModule,
    FluidModule,
    InputGroupModule,
    InputGroupAddonModule,
  ],
})
export class ReviewComponent implements OnInit {
  reviews: Review[] = [];
  review: Review = {
    reviewId: 0,
    productId: 0,
    customerId: 0,
    rating: 0,
    comment: '',
    reviewDate: '',
  };
  loading = false;
  msgs: any[] = [];

  constructor(private reviewService: ReviewService) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.reviewService.getAll().subscribe({
      next: (data) => {
        this.reviews = data;
        this.loading = false;
      },
      error: (err) => {
        this.handleHttpError(err, 'loading reviews');
        this.loading = false;
      },
    });
  }

  saveReview() {
    if (this.review.reviewId) {
      this.reviewService.update(this.review.reviewId, this.review).subscribe({
        next: () => {
          this.showSuccess('Review updated');
          this.resetForm();
          this.loadReviews();
        },
        error: (err) => this.handleHttpError(err, 'updating review'),
      });
    } else {
      this.reviewService.create(this.review).subscribe({
        next: () => {
          this.showSuccess('Review created');
          this.resetForm();
          this.loadReviews();
        },
        error: (err) => this.handleHttpError(err, 'creating review'),
      });
    }
  }

  editReview(r: Review) {
    this.review = { ...r };
  }

  deleteReview(id: number) {
    this.reviewService.delete(id).subscribe({
      next: () => {
        this.showSuccess('Review deleted');
        this.loadReviews();
      },
      error: (err) => this.handleHttpError(err, 'deleting review'),
    });
  }

  resetForm() {
    this.review = {
      reviewId: 0,
      productId: 0,
      customerId: 0,
      rating: 0,
      comment: '',
      reviewDate: '',
    };
  }

  showSuccess(msg: string) {
    this.msgs = [{ severity: 'success', text: msg }];
  }

  showError(msg: string) {
    this.msgs = [{ severity: 'error', text: msg }];
  }

  handleHttpError(err: any, action: string) {
    if (err.status === 401 || err.status === 403) {
      this.showError('Authentication error: You are not authorized.');
    } else if (err.status === 400 && err.error && err.error.error) {
      this.showError('Validation error: ' + err.error.error);
    } else if (err.status === 400 && err.error && typeof err.error === 'string') {
      this.showError('Validation error: ' + err.error);
    } else {
      this.showError('Error ' + action);
    }
  }
}
