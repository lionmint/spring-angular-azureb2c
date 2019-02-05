import { Component, OnInit } from '@angular/core';
import { ActivatedRoute,Router } from '@angular/router';
import { Adal4Service } from 'adal-angular4';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private adalSvc: Adal4Service) { }

  ngOnInit() {
    const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';  
    if (this.adalSvc.userInfo.authenticated) {  
      this.router.navigate([returnUrl]);  
    } else {  
      this.adalSvc.login();  
    }  
  }
}
