import { Routes } from '@angular/router';
import { CustomerComponent } from './customer/customer.component';
import { OrderItemComponent } from './order-item/order-item.component';
import { OrderComponent } from './order/order.component';
import { ProductComponent } from './product/product.component';
import { ReviewComponent } from './review/review.component';

export const routes: Routes = [
  { path: 'customers', component: CustomerComponent },
  { path: 'order-items', component: OrderItemComponent },
  { path: 'orders', component: OrderComponent },
  { path: 'products', component: ProductComponent },
  { path: 'reviews', component: ReviewComponent },
  { path: '', redirectTo: 'customers', pathMatch: 'full' },
];
