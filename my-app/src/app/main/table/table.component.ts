import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { TableDataSource } from './table-datasource';
import { Adal4Service } from 'adal-angular4';
import { HttpClient } from  "@angular/common/http";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: TableDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name'];

  constructor (private adalSvc: Adal4Service,private  httpClient:HttpClient){}

  ngOnInit() {
    this.dataSource = new TableDataSource(this.paginator, this.sort,this.httpClient,this.adalSvc);
  }
}
