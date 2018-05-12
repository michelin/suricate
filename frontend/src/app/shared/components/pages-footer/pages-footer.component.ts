import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-pages-footer',
  templateUrl: './pages-footer.component.html',
  styleUrls: ['./pages-footer.component.css']
})
export class PagesFooterComponent implements OnInit {

  public version: string = environment.VERSION;
  public env: string = environment.ENVIRONMENT;

  constructor() { }

  ngOnInit() {
  }

}
