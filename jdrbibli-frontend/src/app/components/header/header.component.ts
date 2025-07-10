import { ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { filter, Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnDestroy {
  isLoggedIn = false;
  showNavbar = false;

  private subscriptions = new Subscription();

  constructor(private router: Router, private authService: AuthService, private cdr: ChangeDetectorRef) {
    // 🔍 Étape 1 : initialiser selon la valeur actuelle
    this.isLoggedIn = this.authService.isLoggedIn();
    this.updateNavbarVisibility();


    // Étape 2 : s'abonner pour réagir aux changements
    this.subscriptions.add(
      this.authService.isLoggedIn$.subscribe(status => {
        this.isLoggedIn = status;
        this.updateNavbarVisibility();
        this.cdr.detectChanges();
      })
    );

    // Étape 3 : réagir aux changements de route
    this.subscriptions.add(
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe(() => {
        this.updateNavbarVisibility();
      })
    );
  }

  updateNavbarVisibility() {
    const url = this.router.url;
    const publicRoutes = ['/', '/login', '/register', '/reset-password-request', '/reset-password-code', '/reset-password-newpass'];
    this.showNavbar = this.isLoggedIn && !publicRoutes.includes(url);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }
}
