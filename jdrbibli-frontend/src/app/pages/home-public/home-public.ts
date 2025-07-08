import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home-public',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home-public.html',
  styleUrl: './home-public.scss'
})
export class HomePublic {}
