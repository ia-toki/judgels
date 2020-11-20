import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';

import { Card } from '../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { getRatingClass } from '../../../../modules/api/jophiel/userRating';

import './RatingSystemPage.css';

function RatingSystemPage() {
  return (
    <Card title="Rating system">
      <h4>Rating table</h4>
      <HTMLTable className="table-list rating-system-page">
        <thead>
          <tr>
            <th>Rating</th>
            <th>Range</th>
            <th>Division</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className={getRatingClass({ publicRating: 3000, hiddenRating: 3000 })}>legend</td>
            <td>&ge; 3000</td>
            <td>Division 1</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 2500, hiddenRating: 2500 })}>red</td>
            <td>2500 &mdash; 2999</td>
            <td>Division 1</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 2200, hiddenRating: 2200 })}>orange</td>
            <td>2200 &mdash; 2499</td>
            <td>Division 1</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 2000, hiddenRating: 2000 })}>purple</td>
            <td>2000 &mdash; 2199</td>
            <td>Division 1</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 1750, hiddenRating: 1750 })}>blue</td>
            <td>1750 &mdash; 1999</td>
            <td>Division 2</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 1650, hiddenRating: 1650 })}>green</td>
            <td>1650 &mdash; 1749</td>
            <td>Division 2</td>
          </tr>
          <tr>
            <td className={getRatingClass({ publicRating: 0, hiddenRating: 0 })}>gray</td>
            <td>&le; 1649</td>
            <td>Division 2</td>
          </tr>
          <tr>
            <td className={getRatingClass(undefined)}>unrated</td>
            <td>-</td>
            <td>Division 2</td>
          </tr>
        </tbody>
      </HTMLTable>

      <hr />

      <h4>Rating calculation</h4>
      <p>
        Each user has a <strong>public rating</strong> and a <strong>hidden rating</strong>. The public rating is shown
        as the user's current rating, while the hidden rating is hidden. Initially, both ratings are set to{' '}
        <strong>1800</strong>.
      </p>

      <p>
        Suppose that a rated contest consisting of <strong>N</strong> contestants finishes. Let:
      </p>

      <ul>
        <li>
          rank<sub>X</sub> be the rank of X in the contest
        </li>
        <li>
          public<sub>X</sub> be the public rating of X before the contest
        </li>
        <li>
          hidden<sub>X</sub> be the hidden rating of X before the contest
        </li>
      </ul>

      <br />

      <p>Then, the new rating for each contestant A is computed as follows:</p>
      <ol>
        <li>
          <strong>delta</strong> = 0
        </li>
        <li>
          for each other contestant B:
          <ul>
            <li>
              if rank<sub>A</sub> = rank<sub>B</sub>, <strong>delta</strong> is unchanged
            </li>

            <li>
              if rank<sub>A</sub> &lt; rank<sub>B</sub>, <strong>delta</strong> += <strong>score(A, B)</strong>
            </li>
            <li>
              if rank<sub>A</sub> &gt; rank<sub>B</sub>, <strong>delta</strong> -= <strong>score(B, A)</strong>
            </li>
          </ul>
          where <strong>score(A, B)</strong> = max(10, (sigmoid(sqrt(hidden<sub>B</sub> / hidden<sub>A</sub>)) - 0.7)
          &times; log<sub>2</sub> N &times; 1800)
        </li>
        <li>
          <strong>delta</strong> /= <strong>N</strong>
        </li>
        <li>
          <strong>debt</strong> = hiddenA<sub>A</sub> - public<sub>A</sub>
        </li>
        <li>
          if <strong>delta</strong> &ge; 0:
          <ol>
            <li>
              public<sub>A</sub> += 0.2 &times; <strong>delta</strong>
            </li>
            <li>
              <strong>debt</strong> += 0.8 &times; <strong>delta</strong>
            </li>
            <li>
              if <strong>debt</strong> &gt; 0:
              <ol>
                <li>
                  public<sub>A</sub> += <strong>debt</strong>
                </li>
                <li>
                  <strong>debt</strong> = 0
                </li>
              </ol>
            </li>
          </ol>
        </li>
        <li>
          if <strong>delta</strong> &lt; 0:
          <ol>
            <li>
              <strong>debt</strong> += <strong>delta</strong>
            </li>
            <li>
              public<sub>A</sub> += 0.5 &times; <strong>debt</strong>
            </li>
            <li>
              <strong>debt</strong> = 0.5 &times; <strong>debt</strong>
            </li>
          </ol>
        </li>
        <li>
          hidden<sub>A</sub> = public<sub>A</sub> + <strong>debt</strong>
        </li>
        <li>
          public<sub>A</sub> = floor(public<sub>A</sub>)
        </li>
        <li>
          hidden<sub>A</sub> = floor(hidden<sub>A</sub>)
        </li>
      </ol>
    </Card>
  );
}

export default withBreadcrumb('Rating system')(RatingSystemPage);
