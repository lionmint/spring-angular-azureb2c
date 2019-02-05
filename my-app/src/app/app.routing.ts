import { NgModule }  from '@angular/core';  
import { ModuleWithProviders }  from '@angular/core'; 
import { Route, RouterModule } from '@angular/router';
import { MainComponent } from './main/main.component';
import { HomeComponent } from './main/home/home.component';
import { AuthenticationGuard } from './shared/guard/authentication-guard';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TableComponent } from './main/table/table.component';

const routes: Route[] = [
    {  
    path: '',  
    component: MainComponent,  
	canActivate: [AuthenticationGuard],  
    canActivateChild: [AuthenticationGuard],  
	children: [
            {  
                path: '',  
                component: HomeComponent  
            },
            {
                path: 'table',  
                component: TableComponent  
            }
        ]  
    },
    {  
        path: 'login',  
	    component: LoginComponent  
     },
    { 
        path: 'logout',
        component: LogoutComponent
    }];
@NgModule({  
    imports: [RouterModule.forRoot(routes)],  
    exports: [RouterModule]  
}) export class AppRoutingModule {}
    