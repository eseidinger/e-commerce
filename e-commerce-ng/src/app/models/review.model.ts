export interface Review {
  reviewId: number;
  productId: number;
  customerId: number;
  rating: number;
  comment: string;
  reviewDate: Date;
  // Add other fields as needed
}
