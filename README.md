## Strategy for replacing a project module with a module kotlin reusable
- The idea is to use source dependencies as described in `../project-generator/README.md`
- This eventually entails updating project-specification.json in the target project to use source dependencies from kotlin reusable
- Look at both the module in kotlin reusable and the module in the target project
- Decide which behavior in the target project is generic enough to be added to kotlin reusable
- Decide which behavior in the target project is specific to the target project, and move it to another module in the target project
- When behavior overlaps, modify kotlin reusable to have the best of both, so that the target module can import it as a source dependency later
- Refactor the target project to have code identical to kotlin reusable
- Once the models are identical, the target project can be migrated to kotlin reusable

## Note on checked/unchecked splits
usage frequency matters more than method count percentage when deciding on checked/unchecked splits:
- SystemContract: inheritedChannel() is rarely used → no split needed
- FilesContract: Most methods throw checked exceptions AND are frequently used → split valuable
- Process/ProcessBuilder: Only a few methods throw checked exceptions, BUT those methods (start(), waitFor()) are core operations used in virtually every process execution → split is valuable
