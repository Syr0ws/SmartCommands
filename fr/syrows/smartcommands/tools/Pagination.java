package fr.syrows.smartcommands.tools;

public class Pagination {

    private int page, elementsPerPage, totalOfElements;

    public Pagination(int page, int elementsPerPage, int totalOfElements) {

        this.elementsPerPage = elementsPerPage;
        this.totalOfElements = totalOfElements;

        if(page < getFirstPage()) page = getFirstPage();
        else if(page > getLastPage()) page = getLastPage();

        this.page = page;
    }

    public int getCurrentPage() {
        return this.page;
    }

    public boolean isFirstPage() {
        return page == getFirstPage();
    }

    public boolean isLastPage() {
        return page == getLastPage();
    }

    public int getStartingIndex() {
        return this.elementsPerPage * (this.page - 1);
    }

    public int getFirstPage() {
        return 1;
    }

    public int getLastPage() {
        return (int) Math.ceil((double) this.totalOfElements / (double) this.elementsPerPage);
    }

    public int countElements() {

        int elements = this.totalOfElements % this.elementsPerPage;

        return elements == 0 || !isLastPage() ? this.elementsPerPage : elements;
    }

    public int getElementsPerPage() {
        return this.elementsPerPage;
    }

    public int getTotalOfElements() {
        return this.totalOfElements;
    }
}
