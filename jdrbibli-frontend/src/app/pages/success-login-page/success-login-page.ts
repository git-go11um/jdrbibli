import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-success-login-page',
  standalone: true,
  templateUrl: './success-login-page.html',
  styleUrl: './success-login-page.scss',
})
export class SuccessLoginPage implements OnInit {
  constructor(private router: Router) { }

  ngOnInit() {
    this.router.navigate(['/home']);
  }
}