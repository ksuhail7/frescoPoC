import {Component, OnInit} from "@angular/core";
import {Store} from "./store";
import {StoreService} from "./store.service";
@Component({
  selector: 'store-component',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})

export class StoreComponent implements OnInit {
  constructor(private storeService: StoreService) {
  }

  stores: Store[];

  model = new Store("", "", -1);

  ngOnInit(): void {
    this.getAllStores();
  }

  getAllStores(): void {
    this.storeService
      .getAllStores()
      .then(stores => {
        console.debug("store list ", stores);
        this.stores = stores;
      });
  }

}
