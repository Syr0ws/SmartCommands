package fr.syrows.smartcommands.tools;

public class Pagination {

    private int elementsPerPage, totalOfElements;

    public Pagination(int elementsPerPage, int totalOfElements) {
        this.elementsPerPage = elementsPerPage;
        this.totalOfElements = totalOfElements;
    }

    public boolean isFirstPage(int page) {
        return page == getFirstPage();
    }

    public boolean isLastPage(int page) { return page == getLastPage(); }

    public int getStartingIndex(int page) {
        return this.elementsPerPage * (page - 1);
    }

    public int getFirstPage() {
        return 1;
    }

    public int getLastPage() {
        return (int) Math.ceil((double) totalOfElements / (double) elementsPerPage);
    }

    public int countElementsAt(int page) {
        return isLastPage(page) ? this.totalOfElements % elementsPerPage : this.elementsPerPage;
    }

    public int getElementsPerPage() {
        return this.elementsPerPage;
    }

    public int getTotalOfElements() {
        return this.totalOfElements;
    }
}
