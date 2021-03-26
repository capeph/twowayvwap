
VWAP Calculator

This is an implementation of the two-way vwap calculator.

Explanation:
- The calculator will for each price update return an average of the latest prices from all markets. This average will be calculated using a volume veighted average (VWAP).
Each update comes in with a state flag telling if the price provided is indicative or firm.  If any of the prices included in the average is indicative the output will also be marked as indicative.

Error Handling:
- If the input values contain NULL values or invalid prices the calculator will throw an IllegalArgumentException with an explanatory text. No attempt is made to recover from the error, but the previous state is kept in the system.

Assumptions:
- Any update with zero price or zero volume will be ignored.  This is handled on a per side basis, allowing Bid or offer-only updates.
- VWAP is usually used to describe taking an average over a time-series of values. In this case it is used to calculate the average over a number of snapshots
- The supported instruments and markets are hardcoded in the system in the form of provided Enums, no provision is made for adding or removing either.
- The limited number of instruments and markets has influenced the deign choices (keeping everything in maps etc.). Given a different representation of instruments some of those choices would have to be revisited.

Testing:
- There is a set of unit tests for the implemented classes covering the basic functionality
- A very simple micro benchmark is provided.

Usage:
- The class CalculatorImpl contains the implementation of the Calculator interface.
- The TwoWayPriceImpl is an implementation of the TwoWayPrice used as return values from the Calculator.


