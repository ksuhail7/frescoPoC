import { FrescoportalPage } from './app.po';

describe('frescoportal App', function() {
  let page: FrescoportalPage;

  beforeEach(() => {
    page = new FrescoportalPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
