import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';
import { WhoAmIService } from './whoami.service';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterModule, MenubarModule, ButtonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  protected readonly title = signal('e-commerce-ng');
  username: string | null = null;

  menuItems: MenuItem[] = [
    { label: 'Customers', icon: 'pi pi-fw pi-users', routerLink: '/customers' },
    { label: 'Products', icon: 'pi pi-fw pi-box', routerLink: '/products' },
    { label: 'Orders', icon: 'pi pi-fw pi-shopping-cart', routerLink: '/orders' },
    { label: 'Order Items', icon: 'pi pi-fw pi-shopping-cart', routerLink: '/order-items' },
    { label: 'Reviews', icon: 'pi pi-fw pi-star', routerLink: '/reviews' },
  ];

  // Make auth public for template access
  constructor(
    public auth: AuthService,
    private whoAmI: WhoAmIService,
  ) {}

  async ngOnInit() {
    await this.auth.init();
    this.whoAmI.getUsername().subscribe({
      next: (name) => (this.username = name),
      error: () => (this.username = null),
    });
  }
}
