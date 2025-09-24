import { Routes } from '@angular/router';
import { CustomerComponent } from './customer/customer.component';
import { OrderComponent } from './order/order.component';
import { OrderItemComponent } from './order-item/order-item.component';
import { ProductComponent } from './product/product.component';
import { ReviewComponent } from './review/review.component';

export const routes: Routes = [
  { path: 'customers', component: CustomerComponent },
  { path: 'orders', component: OrderComponent },
  { path: 'order-items', component: OrderItemComponent },
  { path: 'products', component: ProductComponent },
  { path: 'reviews', component: ReviewComponent },
  { path: '', redirectTo: 'customers', pathMatch: 'full' },
];
