import { Injectable } from '@angular/core'; 
import { Router, CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Adal4Service } from 'adal-angular4';

@Injectable()
export class AuthenticationGuard implements CanActivate, CanActivateChild {  
    
    constructor(
        private router: Router, 
        private adalSvc: Adal4Service) { }
 
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {  
        if (this.adalSvc.userInfo.authenticated) {  
            return true;  
        } else {  
            this.router.navigate(['/login'], {  
                queryParams: {  
                    returnUrl: state.url  
                } 
            });  
            return false;
        }
    }

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {  
        return this.canActivate(childRoute, state);  
    }  
}  
    