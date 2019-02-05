import { Component, OnInit } from '@angular/core';
import { Adal4Service } from 'adal-angular4';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  isAuthenticated = false;  

  constructor(private adalSvc: Adal4Service) {
    this.isAuthenticated = this.adalSvc.userInfo.authenticated;
  }

  ngOnInit() {
    if (this.isAuthenticated) {
      console.log("Login out...");
      this.adalSvc.logOut();  
    }
  }
}
