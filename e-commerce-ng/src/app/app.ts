import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';
import { WhoAmIService } from './whoami.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  protected readonly title = signal('e-commerce-ng');
  username: string | null = null;

  // Make auth public for template access
  constructor(public auth: AuthService, private whoAmI: WhoAmIService) { }

  async ngOnInit() {
    await this.auth.init();
    this.whoAmI.getUsername().subscribe({
      next: (name) => this.username = name,
      error: () => this.username = null
    });
  }
}
