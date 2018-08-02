import * as React from 'react';

import { Card } from 'components/Card/Card';
import { withBreadcrumb } from 'components/BreadcrumbWrapper/BreadcrumbWrapper';
import { getRatingClass } from 'modules/api/jophiel/userRating';

import './RatingSystemPage.css';

const RatingSystemPage = () => (
  <Card title="Rating system">
    <table className="bp3-html-table table-list rating-system-page">
      <thead>
        <tr>
          <th>Rating</th>
          <th>Range</th>
          <th>Division</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td className={getRatingClass(3000)}>legend</td>
          <td>&ge; 3000</td>
          <td>Division 1</td>
        </tr>
        <tr>
          <td className={getRatingClass(2500)}>red</td>
          <td>2500 &mdash; 2999</td>
          <td>Division 1</td>
        </tr>
        <tr>
          <td className={getRatingClass(2200)}>orange</td>
          <td>2200 &mdash; 2499</td>
          <td>Division 1</td>
        </tr>
        <tr>
          <td className={getRatingClass(2000)}>purple</td>
          <td>2000 &mdash; 2199</td>
          <td>Division 1</td>
        </tr>
        <tr>
          <td className={getRatingClass(1750)}>blue</td>
          <td>1750 &mdash; 1999</td>
          <td>Division 2</td>
        </tr>
        <tr>
          <td className={getRatingClass(1650)}>green</td>
          <td>1650 &mdash; 1749</td>
          <td>Division 2</td>
        </tr>
        <tr>
          <td className={getRatingClass(0)}>gray</td>
          <td>&le; 1649</td>
          <td>Division 2</td>
        </tr>
        <tr>
          <td className={getRatingClass(undefined)}>unrated</td>
          <td>-</td>
          <td>Division 2</td>
        </tr>
      </tbody>
    </table>
  </Card>
);

export default withBreadcrumb('Rating system')(RatingSystemPage);
