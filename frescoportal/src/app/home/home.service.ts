import {Headers, Http} from "@angular/http";
import {Injectable} from "@angular/core";

import {Repository} from './repository';

import 'rxjs/add/operator/toPromise';

@Injectable()
export class HomeService {

  constructor(private http: Http) {}

  private headers = new Headers({'Content-Type': 'application/json'});

  private frescoUrl = 'http://localhost:9092';

  url = `${this.frescoUrl}/repository`;

  create(repo: Repository): Promise<Repository> {
    console.debug("sending request ", JSON.stringify(repo));
    return this.http.post(this.url, JSON.stringify(repo), {headers: this.headers})
      .toPromise().
      then(res => {
        console.debug('received response ', res);
        //TODO: check the response and return repo accordingly.
        return repo;
      })
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occured', error);
    return Promise.reject(error.message || error);
  }

  getAllRepositories(): Promise<Repository[]> {
    return this.http
      .get(this.url)
      .toPromise()
      .then(response => {
        console.debug('response ' , response.json());
        return response.json() as Repository[];})
      .catch(this.handleError);
  }

}
