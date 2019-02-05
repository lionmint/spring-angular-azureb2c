import { Component,OnInit } from '@angular/core';
import { Adal4Service } from 'adal-angular4';  
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private adalSvc: Adal4Service) {  
    this.adalSvc.init(environment.adalConfig);
  }  
  
  ngOnInit(): void {  
    this.adalSvc.handleWindowCallback();  
    if (this.adalSvc.userInfo.authenticated) {  
      const userInfo = this.adalSvc.userInfo;  
      console.log("On Init:: " + JSON.stringify(userInfo));  
    }  
  }  

}
