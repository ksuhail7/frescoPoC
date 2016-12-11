import {Injectable} from "@angular/core";
import {Http, Headers} from "@angular/http";
import {Store} from "./store";

import 'rxjs/add/operator/toPromise';

@Injectable()
export class StoreService {
  constructor(private http: Http) {
  }

  private headers = new Headers({'Content-Type': 'application/json'});

  private frescoUrl = 'http://localhost:9092';

  url = `${this.frescoUrl}/stores`;

  getAllStores(): Promise<Store[]> {
    return this.http
      .get(this.url)
      .toPromise()
      .then(response => {
        console.debug("response ", response);
        return response.json() as Store[];
      }).catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occured', error);
    return Promise.reject(error.message || error);
  }


}
